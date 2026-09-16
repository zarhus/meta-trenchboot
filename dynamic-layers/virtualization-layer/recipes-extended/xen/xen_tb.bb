# nooelint: oelint.var.mandatoryvar.HOMEPAGE,oelint.var.mandatoryvar.SUMMARY,oelint.var.mandatoryvar.LICENSE

require xen-tb.inc
# nooelint: oelint.file.requirenotfound
require recipes-extended/xen/xen.inc
# nooelint: oelint.file.requirenotfound
require recipes-extended/xen/xen-hypervisor.inc

SRC_URI:append = " \
    file://0001-x86-slaunch-release-the-PSP-TMR-once-the-AMD-IOMMU-i.patch \
    file://0002-x86-slaunch-lock-the-DRTM-TPM-localities-on-reboot-a.patch \
    file://xen.cfg \
"
# Seed Xen's configuration to exclude debug information as it breaks something
# in case of Xen.efi and fails like this (not TrenchBoot-related):
# (XEN) *** Building a PV Dom0 ***
# (XEN) ELF: not an ELF binary
# (XEN)
# (XEN) ****************************************
# (XEN) Panic on CPU 0:
# (XEN) Could not construct d0
# (XEN) ****************************************
SRC_URI:append = " file://fragment.cfg"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

do_deploy:append() {
    install -d ${DEPLOYDIR}
    install -m 0664 ${WORKDIR}/xen.cfg ${DEPLOYDIR}
}
