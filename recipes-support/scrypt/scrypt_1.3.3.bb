SUMMARY = "A simple password-based encryption utility"
DESCRIPTION = " \
    The scrypt key derivation function was originally developed \
    for use in the Tarsnap online backup system and is designed to be far more \
    secure against hardware brute-force attacks than alternative functions such \
    as PBKDF2 or bcrypt. \
"
HOMEPAGE = "https://www.tarsnap.com/scrypt.html"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=f969c7aaa49089b1e5f2a7cba002f00f"

DEPENDS = "openssl"

SRC_URI = "git://github.com/Tarsnap/scrypt.git;protocol=https;branch=master"
SRCREV = "041a2126130c3d1e7e2b8facb218c6c017b6890a"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
