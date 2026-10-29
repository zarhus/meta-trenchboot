LIC_FILES_CHKSUM ?= "file://COPYING;md5=d1a1e216f80b6d8da95fec897d0dbec9"

PV = "${XEN_REL}+tb"

SRC_URI = "git://github.com/TrenchBoot/xen.git;branch=${XEN_BRANCH};protocol=https"

SRCREV = "e843e9bd82b8199651924e587915e9290e43ebc8"

S = "${WORKDIR}/git"

XEN_REL = "4.23"
XEN_BRANCH = "tb-staging"
