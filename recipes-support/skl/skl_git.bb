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
    file://0003-pci.c-use-type-1-accesses-when-MMIO-config-space-lie.patch \
    file://0004-link.lds-align-the-measured-length-to-16-bytes.patch \
    file://0005-Makefile-sign-with-a-PSS-salt-of-the-digest-length.patch \
    file://0006-head.S-list-family-1Ah-in-the-SOC-flags.patch \
    file://0007-tpmlib-add-TPM2_PCR_Read.patch \
    file://0008-amdsl-print-the-launch-inputs-and-PCR-17-around-DRTM.patch \
    file://0009-amdsl-hold-no-TPM-locality-during-DRTM_CMD_LAUNCH.patch \
    file://0010-amdsl-dump-TPM_ACCESS-and-read-the-PCRs-at-two-local.patch \
    file://0011-amdsl-print-the-whole-image-hash-and-the-TMR-inputs-.patch \
    file://0012-tpmlib-store-the-last-byte-of-a-TIS-response-at-the-.patch \
    file://0013-psp-wait-up-to-two-minutes-for-DRTM_CMD_EXTEND_OSSL_.patch \
    file://0014-amdsl-read-PCR-17-to-20-after-DRTM_CMD_EXTEND_OSSL_D.patch \
    file://0015-amdsl-dump-the-TIS-state-and-retry-locality-2-before.patch \
    file://0016-amdsl-print-the-SHA-256-of-the-DLME-range-before-the.patch \
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
