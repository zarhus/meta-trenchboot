SUMMARY = "Multi-boot module to perform measured / verified launch using Intel (R) Trusted Execution Technology (Intel(R) TXT)."
DESCRIPTION = " \
    Trusted Boot (tboot) is an open source, pre-kernel/VMM module \
    that uses Intel(R) Trusted Execution Technology (Intel(R) TXT) to perform \
    a measured and verified launch of an OS kernel/VMM."
HOMEPAGE = "http://sourceforge.net/projects/tboot/"
SECTION = "bootloaders"
PR = "r0"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=b86c2f3e88dffdbc026c71e2a818c70c"
DEPENDS += "libtspi openssl zlib"
DEPENDS:class-native += "trousers-native openssl-native zlib-native"

SRC_URI = " \
    https://sourceforge.net/projects/${BPN}/files/${BPN}-${PV}.tar.gz \
"

COMPATIBLE_HOST = '(x86_64|i.86).*-(linux|freebsd.*)'

inherit deploy

EXTRA_OEMAKE = "-e MAKEFLAGS= debug=y"

do_compile() {
    # keep the OE TARGET_ARCH from confusing tboots Makefiles
    if [ "${TARGET_ARCH}" != "x86_64" ]; then
        TMP_TARGET_ARCH="x86_32"
    else
        TMP_TARGET_ARCH="x86_64"
    fi
    oe_runmake SUBDIRS=tboot CC="${HOST_PREFIX}gcc ${TOOLCHAIN_OPTIONS}" CPP="${HOST_PREFIX}cpp ${TOOLCHAIN_OPTIONS}" LDFLAGS="" CFLAGS=""
    oe_runmake SUBDIRS="safestringlib lcptools-v2 tb_polgen utils docs" CFLAGS+="-std=c99" TARGET_ARCH="${TMP_TARGET_ARCH}"
}

do_install() {
    oe_runmake DISTDIR="${D}${base_prefix}" install
}

do_deploy() {
    install -m 0644 ${D}/boot/tboot.gz ${DEPLOYDIR}/tboot-${MACHINE}.gz
}

do_deploy:class-native() {
    :
}

sysroot_stage_all:append:class-target() {
    sysroot_stage_dir ${D}/boot ${SYSROOT_DESTDIR}/kernel
}

addtask deploy after do_install before do_build
addtask deploy after do_install before do_build

PACKAGES =+ " \
    ${PN}-grub \
    ${PN}-lcptools-v2 \
    ${PN}-tbpolgen \
    ${PN}-utils \
"

FILES:${PN} += "${base_prefix}/boot/tboot.gz"
FILES:${PN}-dbg += "${base_prefix}/boot/tboot-syms"
FILES:${PN}-grub += "${sysconfdir}/grub.d"
FILES:${PN}-lcptools-v2 += " \
    ${sbindir}/lcp2_crtpol \
    ${sbindir}/lcp2_crtpolelt \
    ${sbindir}/lcp2_crtpollist \
    ${sbindir}/lcp2_mlehash \
"
FILES:${PN}-tbpolgen += "${sbindir}/tb_polgen"
FILES:${PN}-utils += " \
    ${sbindir}/txt-acminfo \
    ${sbindir}/txt-parse_err \
    ${sbindir}/txt-stat \
"

BBCLASSEXTEND = "native"
