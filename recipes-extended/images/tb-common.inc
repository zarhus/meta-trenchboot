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

# To make do_image_wic generate different partition PARTUUID from other Yocto
# projects. This is workaround for possible clash that might result in firmware
# not showing duplicate boot partitions or mounting wrong partitions if done
# with the use of PARTUUID
SOURCE_DATE_EPOCH = "1234"
