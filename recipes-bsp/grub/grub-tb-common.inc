FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

PV = "2.12"

SRC_URI = " \
           git://github.com/TrenchBoot/grub.git;name=grub;branch=${BRANCH_grub};protocol=https \
           git://github.com/coreutils/gnulib;name=gnulib;branch=${BRANCH_gnulib};protocol=https;destsuffix=git/gnulib \
           file://0001-psp.c-Set-MSI-PRO-B850-and-Turin-CCP-ASP-PSP-version.patch \
          "

BRANCH_grub = "tb-dev"
SRCREV_grub = "8316e83d7672a6cb309e5a5d48440ea649fe438f"

# We add gnulib to SRC_URI to avoid downloading it during the do_configure().
# The bootstrap script seems to be smart enough to skip fetching repo again
# if the local revision is correct. Another way to "solve" that would be
# to add gnulib as a submodule in our fork.
#
# We need to update this with each grub rebase, to make sure it is in sync
# with the bootstrap.conf, otherwise it will be sync during do_configure()
# again.
BRANCH_gnulib = "master"
SRCREV_gnulib = "e87d09bee37eeb742b8a34c9054cd2ebde22b835"

SRCREV_FORMAT = "grub_gnulib"

TARGET_CC_ARCH += "${LDFLAGS}"

S = "${WORKDIR}/git"

do_configure:prepend() {
    cd ${S}
    ${S}/bootstrap
    ${S}/autogen.sh
}

do_configure:append() {
    # Work around a workaround in poky!
    #
    # Tarballs of 2.12 are missing this file, so poky creates it not taking into
    # account that this breaks out-of-tree builds: Make finds the file as
    # "extra_deps.lst" in the source tree for its dependency checking through
    # VPATH, but shell commands run in the build tree don't check the source
    # tree and just fail.
    rm ${S}/grub-core/extra_deps.lst
}
