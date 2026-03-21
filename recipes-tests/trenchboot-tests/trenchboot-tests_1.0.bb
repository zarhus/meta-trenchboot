SUMMARY = "TrenchBoot cukinia tests"
HOMEPAGE = "https://github.com/3mdeb/meta-trenchboot"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://cukinia.conf"

S = "${UNPACKDIR}"

RDEPENDS:${PN} = "cukinia"

do_install() {
  install -d ${D}${sysconfdir}/cukinia
  install -m 0644 ${S}/cukinia.conf ${D}${sysconfdir}/cukinia/
}
