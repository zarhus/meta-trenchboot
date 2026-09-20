LIC_FILES_CHKSUM ?= "file://COPYING;md5=d1a1e216f80b6d8da95fec897d0dbec9"

PV = "${XEN_REL}+tb"

SRC_URI = "git://github.com/TrenchBoot/xen.git;branch=${XEN_BRANCH};protocol=https"

SRCREV = "e1fffe38308c5b1d33e213797d5d5b0239286abc"

S = "${WORKDIR}/git"

XEN_REL = "4.23"
XEN_BRANCH = "tb-staging-2026-07-23"
