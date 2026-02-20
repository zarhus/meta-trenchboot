require tb-common.inc

# Based on xen-image-minimal from meta-virtualization

# Linux kernel option CONFIG_XEN_PCIDEV_BACKEND depends on X86
XEN_PCIBACK_MODULE = ""
XEN_PCIBACK_MODULE:x86 = "kernel-module-xen-pciback"
XEN_PCIBACK_MODULE:x86-64 = "kernel-module-xen-pciback"
XEN_ACPI_PROCESSOR_MODULE = ""
XEN_ACPI_PROCESSOR_MODULE:x86 = "kernel-module-xen-acpi-processor"
XEN_ACPI_PROCESSOR_MODULE:x86-64 = "kernel-module-xen-acpi-processor"

XEN_KERNEL_MODULES ?= " \
                       kernel-module-xen-blkback kernel-module-xen-gntalloc kernel-module-tun \
                       kernel-module-xen-gntdev kernel-module-xen-netback kernel-module-xen-wdt \
                       ${@bb.utils.contains('MACHINE_FEATURES', 'pci', "${XEN_PCIBACK_MODULE}", '', d)} \
                       ${@bb.utils.contains('MACHINE_FEATURES', 'acpi', '${XEN_ACPI_PROCESSOR_MODULE}', '', d)} \
                      "

IMAGE_INSTALL:append = " \
                        ${XEN_KERNEL_MODULES} \
                        xen-tools \
                        qemu \
                        kernel-image \
                        kernel-vmlinux \
                        "

do_image_wic[depends] += "xen:do_deploy"

do_check_xen_state() {
    if [ "${@bb.utils.contains('DISTRO_FEATURES', 'xen', ' yes', 'no', d)}" = "no" ]; then
        die "DISTRO_FEATURES does not contain 'xen'"
    fi
}

addtask check_xen_state before do_rootfs
