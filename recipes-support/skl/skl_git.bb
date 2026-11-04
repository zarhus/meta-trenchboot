SUMMARY = "TrenchBoot Secure Kernel Loader"
DESCRIPTION = "Open source implementation of Secure Loader for AMD Secure Startup."

require skl.inc

do_install:append() {
    install -m 0755 ${S}/extend_all.sh ${D}${bindir}/skl/
    install -m 0755 ${S}/util.sh ${D}${bindir}/skl/
}
