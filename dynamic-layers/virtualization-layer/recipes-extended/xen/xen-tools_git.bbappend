FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:remove = "file://0001-python-pygrub-pass-DISTUTILS-xen-4.18.patch"
SRC_URI:append = " file://0001-python-pygrub-pass-DISTUTILS-xen-4.20.patch"

XEN_REL = "4.22"
XEN_BRANCH = "staging"
# nooelint: oelint.append.protvars
SRCREV = "a3a1e61ce9a00c5a0c8003bad8f1285360399cf4"

PACKAGES += " \
    ${PN}-libxenmanage \
    ${PN}-libxenmanage-dev \
    ${PN}-xen-9pfsd \
"

FILES:${PN}-test += " \
    ${libdir}/xen/tests/test-xenstore \
    ${libdir}/xen/tests/test-rangeset \
    ${libdir}/xen/tests/test-resource \
    ${libdir}/xen/tests/test-domid \
    ${libdir}/xen/tests/test-paging-mempool \
    ${libdir}/xen/tests/test_vpci \
    ${libdir}/xen/tests/test-pdx-offset \
    ${libdir}/xen/tests/test-pdx-mask \
    ${libdir}/xen/tests/test-cpu-policy \
    ${libdir}/xen/tests/test-tsx \
    ${libdir}/xen/tests/test-mem-claim \
    ${libdir}/xen/tests/test_x86_emulator \
"

FILES:${PN}-staticdev += "${libdir}/libxenmanage.a"
FILES:${PN}-xen-9pfsd += "${libdir}/xen/bin/xen-9pfsd"
FILES:${PN}-xen-watchdog += "${systemd_unitdir}/system-sleep/xen-watchdog-sleep.sh"
FILES:${PN}-libxenmanage += "${libdir}/libxenmanage.so.*"
FILES:${PN}-libxenmanage-dev += " \
    ${libdir}/libxenmanage.so \
    ${libdir}/pkgconfig/xenmanage.pc \
    ${datadir}/pkgconfig/xenmanage.pc \
"

RDEPENDS:${PN} += "${PN}-libxenmanage"
