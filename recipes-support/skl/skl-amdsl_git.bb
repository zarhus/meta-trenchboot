SUMMARY = "TrenchBoot Secure Kernel Loader for the AMD PSP DRTM service"
DESCRIPTION = "The Secure Kernel Loader built with AMDSL=y, which measures \
               through the PSP DRTM mailbox and needs a PSP to launch."

require skl.inc

EXTRA_OEMAKE += "AMDSL=y"

SKL_DEPLOY_NAME = "skl-amdsl.bin"
