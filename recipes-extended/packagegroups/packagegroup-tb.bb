SUMMARY = "Trenchboot support packagegroup"
DESCRIPTION = "Trenchboot support packagegroup"

inherit packagegroup

PACKAGES = " \
            ${PN}-base \
            ${PN}-utils \
            ${PN}-tests \
            "

RDEPENDS:${PN}-base += " \
                        kernel-modules \
                        skl \
                        intel-sinit-acm \
                        aem \
                        "

RDEPENDS:${PN}-utils += " \
                         packagegroup-security-tpm2 \
                         util-linux-bash-completion \
                         vim \
                         rsync \
                         kexec \
                         gawk \
                         "

RDEPENDS:${PN}-tests = " \
    trenchboot-tests \
"
