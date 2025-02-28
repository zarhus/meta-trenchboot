LIC_FILES_CHKSUM ?= "file://COPYING;md5=d1a1e216f80b6d8da95fec897d0dbec9"

PV = "${XEN_REL}+tb"

SRC_URI = "git://github.com/TrenchBoot/xen.git;branch=${XEN_BRANCH};protocol=https"

SRCREV = "0b53c6a5b796f3b1a146f612fd06fa37d1bf7714"

S = "${WORKDIR}/git"

XEN_REL = "4.17"
XEN_BRANCH = "aem_intel_fixes_v0.4.1"
