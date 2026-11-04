# Shared by skl (classic SKINIT launch) and skl-amdsl (the build that
# drives the PSP DRTM service).

HOMEPAGE = "https://github.com/TrenchBoot/secure-kernel-loader"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4641e94ec96f98fabc56ff9cc48be14b"

# coreutils, openssl and xxd sign the AMDSL build.
DEPENDS += "util-linux-native coreutils-native openssl-native xxd-native"

SRC_URI = "git://github.com/TrenchBoot/secure-kernel-loader.git;protocol=https;branch=${BRANCH};name=skl"
BRANCH = "skl-loader-amdsl-noblob-sl_header-fix"
SRCREV = "65fbb8708528ec58948cc6282de0e07e5160f8fb"

TUNE_CCARGS:remove = "-msse3 -mfpmath=sse"

S = "${WORKDIR}/git"
FILES:${PN} += "${bindir}/skl /boot"
RDEPENDS:${PN} = "bash"

EXTRA_OEMAKE += "DEBUG=y"
SECURITY_STACK_PROTECTOR = ""
lcl_maybe_fortify = ""

# Name of the binary in the rootfs, in DEPLOY_DIR_IMAGE and so on the boot
# partition.
SKL_DEPLOY_NAME ?= "skl.bin"

do_install(){
    install -d ${D}${bindir}/skl
    install -m 0600 ${S}/skl.bin ${D}${bindir}/skl/${SKL_DEPLOY_NAME}
}

inherit deploy

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0600 ${S}/skl.bin ${DEPLOYDIR}/${SKL_DEPLOY_NAME}
}

addtask do_deploy after do_compile before do_build
