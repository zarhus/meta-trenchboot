require grub-tb-common.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://cfg \
    file://0001-slaunch-keep-the-kernel-s-decompression-area-free-on.patch \
    file://0002-slaunch-txt-take-the-Linux-MLE-size-from-the-MLE-hea.patch \
    file://0003-slaunch-txt-start-the-high-PMR-at-4G.patch \
"

# nooelint: oelint.vars.mispell.unknown
GRUB_BUILDIN = " \
    boot linux ext2 fat serial part_msdos part_gpt normal \
    efi_gop iso9660 configfile search loadenv test linux16 \
    slaunch search_label multiboot2 echo ls reboot chain \
    at_keyboard \
"
