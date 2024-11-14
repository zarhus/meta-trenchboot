LIC_FILES_CHKSUM ?= "file://COPYING;md5=d1a1e216f80b6d8da95fec897d0dbec9"

PV = "${XEN_REL}+tb"

SRC_URI = "git://github.com/TrenchBoot/xen.git;branch=${XEN_BRANCH};protocol=https"

SRCREV = "1278ae8b5f51de658b423849ccdfdb1a373acc8a"

S = "${WORKDIR}/git"

XEN_REL = "4.17"
XEN_BRANCH = "aem-next"
