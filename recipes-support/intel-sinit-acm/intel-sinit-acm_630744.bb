SUMMARY = "Production SINIT ACM"
DESCRIPTION = "Production release of the SINIT ACM (authenticated code module)"
HOMEPAGE = "https://www.intel.com/content/www/us/en/developer/articles/tool/intel-trusted-execution-technology.html"
LICENSE = "CLOSED"

UNZIPPED_DIR = "630744_003"
SRC_URI = "file://${UNZIPPED_DIR}.zip"
SRC_URI[sha256sum] = "4a4696bfa855b711416a0fedbe2b1fa9390bde9ce0162e3044132f1b5d328629"

ALLOW_EMPTY:${PN} = "1"

inherit deploy

do_deploy() {
    install -d ${DEPLOYDIR}/acm
    for file in ${WORKDIR}/${UNZIPPED_DIR}/*.bin
    do
        install -m 0600 ${file} ${DEPLOYDIR}/acm/
    done
}

addtask do_deploy after do_install
