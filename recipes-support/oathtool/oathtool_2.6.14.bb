SUMMARY = "One-time password components"
DESCRIPTION = " \
    OATH Toolkit provides components for building one-time \
    password authentication systems. It contains shared libraries, command line \
    tools and a PAM module. Supported technologies include the event-based HOTP \
    algorithm and the time-based TOTP algorithm. \
"
HOMEPAGE = "https://oath-toolkit.codeberg.page/"

LICENSE = "GPL-3.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464 \
    file://COPYING.LESSERv2;md5=4bf661c1e3793e55c8d1051bc5e0ae21 \
"

DEPENDS = "bison-native gengetopt-native"

SRC_URI = " \
    git://codeberg.org/oath-toolkit/oath-toolkit.git;name=oath-toolkit;protocol=https;branch=main \
    git://github.com/coreutils/gnulib.git;name=gnulib;protocol=https;branch=stable-202601;destsuffix=gnulib \
"

SRCREV_oath-toolkit = "5a534218feaa1bf58b59bc183d7d4c34bc48c7c1"
SRCREV_gnulib = "2a288c048e2a23ea9cd8cbef9a60aa4ac82bdc3d"
SRCREV_FORMAT = "oath-toolkit_gnulib"

S = "${WORKDIR}/git"

inherit autotools pkgconfig gettext

do_configure:prepend() {
    export GNULIB_SRCDIR="${WORKDIR}/gnulib"
    export PATH="${WORKDIR}/gnulib:${PATH}"
    (cd "${S}" && ./bootstrap)
}

do_compile:prepend() {
    export GNULIB_SRCDIR="${WORKDIR}/gnulib"
    export PATH="${WORKDIR}/gnulib:${PATH}"

    # help2man cannot run cross-compiled binaries, so provide a stub manpage
    touch ${B}/oathtool/oathtool.1
}

EXTRA_OECONF = "--disable-pam --disable-pskc"
