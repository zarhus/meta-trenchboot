SUMMARY = "TrenchBoot Secure Kernel Loader"
DESCRIPTION = "Open source implementation of Secure Loader for AMD Secure Startup."
HOMEPAGE = "https://github.com/TrenchBoot/secure-kernel-loader"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4641e94ec96f98fabc56ff9cc48be14b"

DEPENDS = "util-linux-native"

SRC_URI = "git://github.com/TrenchBoot/secure-kernel-loader.git;protocol=https;branch=${BRANCH};name=skl"
BRANCH = "skl-loader-amdsl-v11"
SRCREV = "de1899007d038eeacf5edb8e63c0f0a4b3e265c4"

TUNE_CCARGS:remove = "-msse3 -mfpmath=sse"

S = "${WORKDIR}/git"
FILES:${PN} += "${bindir}/skl /boot"
RDEPENDS:${PN} = "bash"

EXTRA_OEMAKE += "DEBUG=y"
SECURITY_STACK_PROTECTOR = ""
lcl_maybe_fortify = ""

do_install(){
    install -d ${D}${bindir}/skl

    install -m 0600 ${S}/skl.bin ${D}${bindir}/skl/
    install -m 0755 ${S}/extend_all.sh ${D}${bindir}/skl/
    install -m 0755 ${S}/util.sh ${D}${bindir}/skl/
}

inherit deploy

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0600 ${S}/skl.bin ${DEPLOYDIR}
}

addtask do_deploy after do_compile before do_build
