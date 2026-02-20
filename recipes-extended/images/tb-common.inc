# nooelint: oelint.file.requirenotfound
require recipes-core/images/core-image-minimal.bb

SUMMARY = "A small image just capable of allowing a device to boot."
DESCRIPTION = "A small image just capable of allowing a device to boot."
LICENSE = "MIT"

IMAGE_FEATURES:append = " ssh-server-openssh"

IMAGE_INSTALL:append = " \
                        packagegroup-tb-base \
                        packagegroup-tb-utils \
                        packagegroup-tb-tests \
                        "

IMAGE_FSTYPES += "wic wic.gz wic.bmap"
