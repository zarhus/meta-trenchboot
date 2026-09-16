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

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

do_deploy:append() {
    install -d ${DEPLOYDIR}
    install -m 0664 ${WORKDIR}/xen.cfg ${DEPLOYDIR}
}
