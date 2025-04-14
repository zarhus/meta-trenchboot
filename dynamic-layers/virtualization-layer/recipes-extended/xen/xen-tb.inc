LIC_FILES_CHKSUM ?= "file://COPYING;md5=d1a1e216f80b6d8da95fec897d0dbec9"

PV = "${XEN_REL}+tb"

SRC_URI = "git://github.com/TrenchBoot/xen.git;branch=${XEN_BRANCH};protocol=https"

SRCREV = "3ea3f9c45feb984a68f68d2cbc98416f72d1fbca"

S = "${WORKDIR}/git"

XEN_REL = "4.17"
XEN_BRANCH = "aem-fix-legacy-txt"
