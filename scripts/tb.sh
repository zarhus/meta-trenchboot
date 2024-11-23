#!/bin/bash

set -Eeu

print_help() {
    cat << EOF
Usage:
    ./tb.sh [OPTION]... {modify|reset|menuconfig|build|deploy}
    1.) ./tb.sh modify recipe
    2.) ./tb.sh reset recipe
    3.) ./tb.sh menuconfig
    4.) ./tb.sh build recipe
    5.) ./tb.sh deploy recipe destination

    1.) Fetches source code for the recipe
    2.) Removes recipe source code from disk
    3.) Opens Linux menuconfig
    4.) Builds recipe from fetched source code.
    5.) Copies built recipe to destination

    OPTION:
        -h|--help               - Print this help
        -v|--verbose            - Verbose output

    recipe:
        skl                     - Secure Kernel Loader
        linux-tb                - Linux kernel
        grub                    - GRUB legacy
        grub-efi                - GRUB EFI
        tb-full-image           - Complete image with all components. Can only
                                  be build or deployed

    destination:
        Path to target root '/' directory with boot partition mounted at
        '/boot'. Uses same syntax as rsync e.g.:

        /mnt                    - locally mounted TrenchBoot rootfs partition at
                                  '/mnt' with boot partition mounted
                                  at '/mnt/boot'
        root@192.168.4.10:/     - remote machine running TrenchBoot
        root@192.168.4.10:/mnt  - remote machine with TrenchBoot rootfs
                                  partition mounted at '/mnt' and boot partition
                                  mounted at '/mnt/boot'
EOF
}

cleanup() {
    popd &>/dev/null || exit 1
}

usage_error() {
    echo "$1"
    print_help
    exit 1
}

error_msg() {
    echo "$1"
    exit 1
}

parse_args() {
    local positional_args=()
    ACTION=
    RECIPE_ARG=
    DESTINATION_ARG=

    while [ $# -gt 0 ]; do
        case $1 in
            -h|--help)
                print_help
                exit 0
                ;;
            -v|--verbose)
                set -x
                ;;
            -*)
                usage_error "Unknown option '$1'"
                ;;
            *)
                positional_args+=("$1")
                ;;
        esac
        shift
    done

    set -- "${positional_args[@]}"

    if [ $# -lt 1 ]; then
        usage_error "Wrong number of positional arguments"
    fi
    ACTION="$1"

    case $ACTION in
        modify|reset|build)
            if [ $# -ne 2 ]; then
                usage_error "Wrong number of arguments"
            fi
            RECIPE_ARG="$2"
            ;;
        deploy)
            if [ $# -ne 3 ]; then
                usage_error "Wrong number of positional arguments"
            fi
            RECIPE_ARG="$2"
            DESTINATION_ARG="$3"
            if [ "$DESTINATION_ARG" == "/" ]; then
                read -p "Destination path is '/', are you sure? [N|y]: " -n 1 -r choice
                echo
                case $choice in
                    y) ;;
                    *) exit 0
                esac
            fi
            ;;
        menuconfig)
            RECIPE_ARG="linux-tb"
            ;;
        *)
            usage_error "Wrong positional argument"
            ;;
    esac

    case $RECIPE_ARG in
        skl|grub|grub-efi|linux-tb|tb-full-image)
            ;;
        *)
            usage_error "Wrong recipe"
            ;;
    esac
}

menuconfig() {
    if [ -d "build/workspace/sources/$RECIPE_ARG" ]; then
        kas-container shell "$KAS_YAML" \
            -c "devtool menuconfig $RECIPE_ARG"
    else
        error_msg "First use ./tb.sh modify linux-tb"
    fi
}

modify_recipe() {
    if [ ! -d "build/workspace/sources/$RECIPE_ARG" ]; then
        kas-container shell "$KAS_YAML" \
            -c "devtool modify $RECIPE_ARG"
    else
        error_msg "Source is already fetched"
    fi
}

reset_recipe() {
    if [ -d "build/workspace/sources/$RECIPE_ARG" ]; then
        kas-container shell "$KAS_YAML" \
            -c "devtool reset $RECIPE_ARG"
    else
        error_msg "Nothing to reset"
    fi
}

build_recipe() {
    if [ "$RECIPE_ARG" == "tb-full-image" ]; then
        rm -rf "build/workspace/sources/grub/gnulib" 2&>/dev/null || true
        rm -rf "build/workspace/sources/grub-efi/gnulib" 2&>/dev/null || true
        kas-container shell "$KAS_YAML" \
            -c "devtool build-image $RECIPE_ARG"
        return
    fi
    if [ ! -d "build/workspace/sources/$RECIPE_ARG" ]; then
        modify_recipe
    elif [ "$RECIPE_ARG" == "grub" ] || [ "$RECIPE_ARG" == "grub-efi" ]; then
        rm -rf "build/workspace/sources/$RECIPE_ARG/gnulib"
    fi

    kas-container shell "$KAS_YAML" \
        -c "devtool build $RECIPE_ARG"
}

update_grub() {
    local grub_dir=/usr/lib/grub
    local remote=
    local remote_path=

    case $DESTINATION_ARG in
        *:*)
            remote="${DESTINATION_ARG%:*}"
            remote_path="${DESTINATION_ARG#*:}"
            grub_dir="$remote_path/$grub_dir"
            # shellcheck disable=SC2016
            disk_device_name='basename $(realpath /sys/class/block/$(basename \
                $(df '$remote_path/boot' | awk "END{print \$1}"))/../)'
            ssh "$remote" "grub-mkimage -p '(hd0,msdos1)/grub' -d $grub_dir/i386-pc \
                -o $grub_dir/i386-pc/core.img -O i386-pc at_keyboard biosdisk boot \
                chain configfile echo ext2 fat linux ls multiboot2 part_msdos reboot serial vga"
            ssh "$remote" "echo \"(hd0) /dev/\$($disk_device_name)\" > /tmp/device.map"
            ssh "$remote" "grub-bios-setup -v --device-map=/tmp/device.map \
                -r \"hd0,msdos1\" -d $grub_dir/i386-pc /dev/\$($disk_device_name)"
            ;;
        *)
            grub_dir="/mnt/$grub_dir"
            boot_device_name="$(basename "$(df "$DESTINATION_ARG/boot" | awk 'END{print $1}')")"
            disk_device="/dev/$(basename "$(realpath "/sys/class/block/$boot_device_name/../")")"
            read -p "Is destination device correct ($disk_device) [N|y]: " -n 1 -r choice
            echo
            case $choice in
                y) ;;
                *)
                    exit 0
                    ;;
            esac
            device_map=$(basename "$(mktemp -p "$WORK_DIR")")
            echo "(hd0) $disk_device" > "$device_map"
            if [ -o xtrace ]; then
                verbose="set -x"
            else
                verbose=":"
            fi
            kas-container --runtime-args \
                "--device=$disk_device:$disk_device -v $DESTINATION_ARG:/mnt" \
                shell "$KAS_YAML" -c " \
                  $verbose &&
                  cd /build/tmp/sysroots-components/x86_64/grub-native/usr &&
                  sudo ./bin/grub-mkimage -p '(hd0,msdos1)/grub' -d $grub_dir/i386-pc \
                    -o $grub_dir/i386-pc/core.img -O i386-pc at_keyboard biosdisk boot \
                    chain configfile ext2 fat linux ls part_msdos reboot serial vga &&
                  sudo ./sbin/grub-bios-setup -v --device-map=/work/$device_map \
                    -r 'hd0,msdos1' -d $grub_dir/i386-pc $disk_device \
                "
            rm "$WORK_DIR/$device_map"
            ;;
    esac
}

deploy_recipe() {
    local kernel_path=
    local device_path=
    local tmp_dir=
    local recipe_version=
    local work_dir="build/tmp/work"
    local deploy_dir="build/tmp/deploy/images/genericx86-64"
    local core_path="$work_dir/core2-64-tb-linux"
    local genericx86_path="$work_dir/genericx86_64-tb-linux"

    recipe_version=$(
        kas-container shell "$KAS_YAML" \
                -c "devtool latest-version $RECIPE_ARG" 2>&1 |
            sed -n 's/INFO: Current version: //p'
        )

    case $DESTINATION_ARG in
        *:*)
            SUDO=
            ;;
        *)
            SUDO=sudo
            ;;
    esac

    case $RECIPE_ARG in
        skl)
            ${SUDO} rsync -chavP "$core_path/skl/$recipe_version/image/" "$DESTINATION_ARG"
            ${SUDO} rsync -chrtvP --inplace "$core_path/skl/$recipe_version/deploy-skl/skl.bin" "$DESTINATION_ARG/boot"
            ;;
        grub)
            ${SUDO} rsync -chavP --exclude "boot" "$core_path/grub/$recipe_version/image/" "$DESTINATION_ARG"
            update_grub
            ;;
        grub-efi)
            ${SUDO} rsync -chavP --exclude "boot" "$core_path/grub-efi/$recipe_version/image/" "$DESTINATION_ARG"
            ${SUDO} rsync -chrtvP --inplace "$core_path/grub-efi/$recipe_version/image/boot/" "$DESTINATION_ARG"/boot
            ;;
        linux-tb)
            kernel_path="$genericx86_path/linux-tb/$recipe_version"
            ${SUDO} rsync -chavP --exclude "boot" \
                "$kernel_path/image/" "$DESTINATION_ARG"
            ${SUDO} rsync -chrtvPL --inplace \
                "$kernel_path/deploy-linux-tb/bzImage-initramfs-genericx86-64.bin" "$DESTINATION_ARG/boot"
            ${SUDO} rsync -chrtvPL --inplace \
                "$kernel_path/deploy-linux-tb/bzImage-initramfs-genericx86-64.bin" "$DESTINATION_ARG/boot/bzImage"
            ;;
        tb-full-image)
            tmp_dir=$(mktemp -d)
            mkdir "$tmp_dir/boot"
            mkdir "$tmp_dir/rootfs"
            device_path=$(sudo losetup --show -Prf "$deploy_dir/tb-full-image-genericx86-64.rootfs.wic")
            # shellcheck disable=SC2064
            trap "set +e ; sudo umount ${device_path}p* ; \
                sudo losetup -d $device_path ; set -e ; cleanup" EXIT
            sudo mount -r -o uid=$UID "${device_path}p1" "$tmp_dir/boot"
            sudo mount -r "${device_path}p2" "$tmp_dir/rootfs"
            sudo rsync -chavP --exclude "boot" --exclude "etc/fstab" \
                "$tmp_dir/rootfs/" "$DESTINATION_ARG"
            sudo rsync -chrtvP --inplace "$tmp_dir/boot/" "$DESTINATION_ARG/boot"
            sudo umount "$tmp_dir"/*
            sudo losetup -d "$device_path"
            trap cleanup EXIT
            rmdir "$tmp_dir/boot" "$tmp_dir/rootfs"
            rmdir "$tmp_dir"
            update_grub
            ;;
    esac
}

trap cleanup EXIT

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
LAYER_DIR="$(dirname "$SCRIPT_DIR")"
WORK_DIR="$(dirname "$LAYER_DIR")"
KAS_YAML="$LAYER_DIR/kas-tb-full.yml"
pushd "$WORK_DIR" &>/dev/null || exit 1
parse_args "$@"

case "$ACTION" in
    modify)
        modify_recipe
        ;;
    reset)
        reset_recipe
        ;;
    menuconfig)
        menuconfig
        ;;
    build)
        build_recipe
        ;;
    deploy)
        deploy_recipe
        ;;
esac
