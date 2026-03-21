require grub-tb-common.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://cfg"

# nooelint: oelint.vars.mispell.unknown
GRUB_BUILDIN = " \
    boot linux ext2 fat serial part_msdos part_gpt normal \
    efi_gop iso9660 configfile search loadenv test linux16 \
    slaunch search_label multiboot2 echo ls reboot chain \
    at_keyboard \
"

S = "${UNPACKDIR}/${BP}"
