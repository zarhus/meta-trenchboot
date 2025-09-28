LIC_FILES_CHKSUM ?= "file://COPYING;md5=d1a1e216f80b6d8da95fec897d0dbec9"

PV = "${XEN_REL}+tb"

SRC_URI = "git://github.com/TrenchBoot/xen.git;branch=${XEN_BRANCH};protocol=https"

SRCREV = "113264e01d44acfe2af59cf220fe7124ab49b9e4"

S = "${UNPACKDIR}/git"

XEN_REL = "4.21-dev"
XEN_BRANCH = "aem-staging-2025-05-30-v3"
