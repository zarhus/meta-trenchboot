SUMMARY = "TPM service wrapper for portable installations"
DESCRIPTION = " \
    Wraps tcsd's systemd service to replace /var/lib/tmp with a symbolic link to \
    /var/lib/tmps/TPMID/.  TPMID is read from an NVRAM entry provisioned from the \
    outside (should be 20 bytes long). \
    \
    Can also be used to manage /var/lib/tpm for TPM2 as long as something will \
    create files in /var/lib/tpm. \
"

HOMEPAGE = "https://github.com/QubesOS/qubes-trousers-changer"

LICENSE = "GPL-2.0-or-later"
# There is no license file, but this one states "GPL" license.
LIC_FILES_CHKSUM = "file://trousers-changer.spec.in;md5=36377945ebeb7479328ed3c4e495b7b4"

SRC_URI = "git://github.com/QubesOS/qubes-trousers-changer.git;protocol=https;branch=main"
SRCREV = "5983773eb3624da0b7d591947d6e23dd748e61bd"

S = "${WORKDIR}/git"

FILES:${PN} += "${systemd_system_unitdir}/tcsd.service.d"

do_install() {
    install -d "${D}${sbindir}" "${D}${systemd_system_unitdir}/tcsd.service.d"
    install -m 0755 "${S}/systemd/system/tcsd.service.d/trousers-changer.conf" "${D}${systemd_system_unitdir}/tcsd.service.d/trousers-changer.conf"
    for file in "${S}/sbin"/*; do
        install -m 0755 "${file}" "${D}${sbindir}"
    done
}

RDEPENDS:${PN} += "bash trousers tpm-tools tpm2-tools tpm-extra psmisc"
