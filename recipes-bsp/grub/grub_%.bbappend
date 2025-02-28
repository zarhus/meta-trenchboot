require grub-tb-common.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-add-root-flag-to-grub-bios-setup.patch"

FILES:${PN}-common += " \
    ${libdir}/grub/i386-pc \
    ${datadir}/bash-completion \
    ${systemd_system_unitdir}/grub-systemd-integration.service \
    ${systemd_system_unitdir}/systemd-logind.service.d \
    ${systemd_system_unitdir}/systemd-logind.service.d/10-grub-logind-service.conf \
"

RDEPENDS:${PN}-common += "diffutils freetype"

INSANE_SKIP:${PN}-common += "arch"
