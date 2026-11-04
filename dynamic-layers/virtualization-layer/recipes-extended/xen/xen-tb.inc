LIC_FILES_CHKSUM ?= "file://COPYING;md5=d1a1e216f80b6d8da95fec897d0dbec9"

PV = "${XEN_REL}+tb"

SRC_URI = "git://github.com/TrenchBoot/xen.git;branch=${XEN_BRANCH};protocol=https"

SRCREV = "544a513e5856413a4f8cc5eef1e4e52fbfc7f8f5"

S = "${WORKDIR}/git"

XEN_REL = "4.23"
XEN_BRANCH = "tb-staging"
