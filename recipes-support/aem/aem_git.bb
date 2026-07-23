SUMMARY = "Anti Evil Maid scripts for QubesOS"
DESCRIPTION = "Event log dump and verification script"
HOMEPAGE = "https://github.com/TrenchBoot/qubes-antievilmaid/"

LICENSE = "GPL-2.0-or-later"
# There is no license file, but this one states "GPL" license.
LIC_FILES_CHKSUM = "file://anti-evil-maid.spec.in;md5=7659fd73309def53b6048214f429b665"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " file://0001-sbin-anti-evil-maid-dump-evt-log-update-for-v4-patch.patch"

SRC_URI = "git://github.com/TrenchBoot/qubes-antievilmaid.git;protocol=https;branch=main"
SRCREV = "18a0a743462f50363ca83a9946bbc8b399a6e6da"

S = "${WORKDIR}/git"

do_install() {
    install -d "${D}${sbindir}"
    for file in "${S}/sbin"/*; do
        install -m 0755 "${file}" "${D}${sbindir}"
    done
}

RDEPENDS:${PN} += "bash gawk"
