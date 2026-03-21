# nooelint: oelint.var.mandatoryvar.HOMEPAGE,oelint.var.mandatoryvar.SUMMARY,oelint.var.mandatoryvar.LICENSE

LIC_FILES_CHKSUM ?= "file://COPYING;md5=d1a1e216f80b6d8da95fec897d0dbec9"

XEN_REL ?= "4.17"
XEN_BRANCH ?= "stable-${XEN_REL}"
PV = "${XEN_REL}+stable"

SRC_URI = " \
    git://xenbits.xen.org/xen.git;branch=${XEN_BRANCH};protocol=https \
    file://10-ether.network \
    file://10-xenbr0.netdev \
    file://10-xenbr0.network \
    file://0001-python-pygrub-pass-DISTUTILS-xen-4.15.patch \
    file://0001-tools-xenstore-xenstored_control.c-correctly-print-t.patch \
"

# xen 4.17.2 release sha
SRCREV ?= "0ebd2e49bcd0f566ba6b9158555942aab8e41332"

INSANE_SKIP:${PN}-dbg = "buildpaths"

# nooelint: oelint.file.requirenotfound
require recipes-extended/xen/xen.inc
# nooelint: oelint.file.requirenotfound
require recipes-extended/xen/xen-tools.inc

RDEPENDS:${PN}:remove = "${PN}-libxenmanage"
