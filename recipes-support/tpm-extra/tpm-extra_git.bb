SUMMARY = "Additional tools not included in tpm-tools package."
HOMEPAGE = "https://github.com/QubesOS/qubes-tpm-extra"

LICENSE = "GPL-2.0-or-later"
# There is no license file, but this one states "GPL" license.
LIC_FILES_CHKSUM = "file://tpm-extra.spec.in;md5=baac70eb35980bb546ca2f3cc12ef4ae"

DEPENDS += "trousers"
SRC_URI = "git://github.com/QubesOS/qubes-tpm-extra.git;protocol=https;branch=main"
SRCREV = "90d4421efcc6efedc91fa483d57167413831d0a2"

S = "${WORKDIR}/git"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} "${S}/tpm_pcr_extend.c" -ltspi -o "${B}/tpm_pcr_extend"
}

do_install() {
    install -d "${D}${sbindir}"
    install -m 0755 "${B}/tpm_pcr_extend" "${D}${sbindir}"
    for file in "${S}/sbin"/*; do
        install -m 0755 "${file}" "${D}${sbindir}"
    done
}

RDEPENDS:${PN} += "bash trousers util-linux-unshare"
