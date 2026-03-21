# nooelint: oelint.var.mandatoryvar.HOMEPAGE,oelint.var.mandatoryvar.SUMMARY,oelint.var.mandatoryvar.LICENSE

require xen-tb.inc
# nooelint: oelint.file.requirenotfound
require recipes-extended/xen/xen.inc
# nooelint: oelint.file.requirenotfound
require recipes-extended/xen/xen-hypervisor.inc

SRC_URI:append = " file://xen.cfg"

do_deploy:append() {
    install -d ${DEPLOYDIR}
    install -m 0664 ${UNPACKDIR}/xen.cfg ${DEPLOYDIR}
}
