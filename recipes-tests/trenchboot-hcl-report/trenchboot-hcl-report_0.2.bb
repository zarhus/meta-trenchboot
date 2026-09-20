SUMMARY = "TrenchBoot HCL report script"
HOMEPAGE = "https://github.com/TrenchBoot/trenchboot-hcl"
SECTION = "tools"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/LGPL-2.1-or-later;md5=2a4f4fd2128ea2f65047ee63fbca9f68"

SRC_URI = "file://trenchboot-hcl-report.sh"

S = "${WORKDIR}/git"

RDEPENDS:${PN} = " \
    bash \
    converged-security-suite-txt \
    cpio \
    dmidecode \
    pciutils \
    tboot-utils \
"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/trenchboot-hcl-report.sh ${D}${bindir}/trenchboot-hcl-report
}
