LIC_FILES_CHKSUM ?= "file://COPYING;md5=d1a1e216f80b6d8da95fec897d0dbec9"

PV = "${XEN_REL}+tb"

SRC_URI = "git://github.com/TrenchBoot/xen.git;branch=${XEN_BRANCH};protocol=https"

# v0.5.0 tag
SRCREV = "ad7b5727bcde383957f69ff03a38ade1c82f531d"

S = "${WORKDIR}/git"

XEN_REL = "4.17"
XEN_BRANCH = "aem-next"
