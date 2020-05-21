require recipes-core/images/core-image-minimal.bb

IMAGE_INSTALL_append = " \
  kernel-modules \
  tpm2-tools \
  tpm2-abrmd \
  tpm2-tss \
  libtss2 \
  libtss2-mu \
  libtss2-tcti-device \
  libtss2-tcti-mssim \
  landing-zone \
  grub-efi \
  dhcp-client \
"

IMAGE_FSTYPES += "wic.gz wic.bmap"
IMAGE_FEATURES_append = " ssh-server-openssh"