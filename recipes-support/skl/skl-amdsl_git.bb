SUMMARY = "TrenchBoot Secure Kernel Loader for the AMD PSP DRTM service"
DESCRIPTION = "The Secure Kernel Loader built with AMDSL=y, which measures \
               through the PSP DRTM mailbox and needs a PSP to launch."

require skl.inc

# Serial debug output of the AMDSL launch from the MSI PRO B850 and
# Gigabyte MZ33-AR1 bring-up. With them the SKL is the one tested on that
# hardware. 0003 and 0006 also request TPM localities to read the PCRs.
SRC_URI += " \
    file://0001-tpmlib-add-TPM2_PCR_Read.patch \
    file://0002-amdsl-print-the-launch-inputs-and-PCR-17-around-DRTM.patch \
    file://0003-amdsl-dump-TPM_ACCESS-and-read-the-PCRs-at-two-local.patch \
    file://0004-amdsl-print-the-whole-image-hash-and-the-TMR-inputs-.patch \
    file://0005-amdsl-read-PCR-17-to-20-after-DRTM_CMD_EXTEND_OSSL_D.patch \
    file://0006-amdsl-dump-the-TIS-state-and-retry-locality-2-before.patch \
    file://0007-amdsl-print-the-SHA-256-of-the-DLME-range-before-the.patch \
"

EXTRA_OEMAKE += "AMDSL=y"

SKL_DEPLOY_NAME = "skl-amdsl.bin"
