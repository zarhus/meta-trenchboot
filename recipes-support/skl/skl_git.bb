SUMMARY = "TrenchBoot Secure Kernel Loader"
DESCRIPTION = "Open source implementation of Secure Loader for AMD Secure Startup."
HOMEPAGE = "https://github.com/TrenchBoot/secure-kernel-loader"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4641e94ec96f98fabc56ff9cc48be14b"

DEPENDS = "util-linux-native coreutils-native openssl-native xxd-native"

SRC_URI = " \
    git://github.com/TrenchBoot/secure-kernel-loader.git;protocol=https;branch=${BRANCH};name=skl \
    file://0001-head.S-move-skl_info-and-bootloader_data-fields-back.patch \
    file://0002-psp.c-Set-MSI-PRO-B850-and-MZ33-AR1-PSP-version.patch \
"

BRANCH = "skl-loader-amdsl-noblob"
SRCREV = "60df73b8cc5d8e729d9eb62e4faba18d8fe3e7f0"

TUNE_CCARGS:remove = "-msse3 -mfpmath=sse"

S = "${WORKDIR}/git"
FILES:${PN} += "${bindir}/skl /boot"
RDEPENDS:${PN} = "bash"

EXTRA_OEMAKE += "DEBUG=y AMDSL=y"
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
