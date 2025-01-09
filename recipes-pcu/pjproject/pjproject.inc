## --*- bitbake -*--
SRC_URI = "https://github.com/pjsip/pjproject/archive/refs/tags/${PV}.tar.gz"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS += "alsa-lib"

inherit autotools-brokensep pkgconfig

LDFLAGS = "-shared"
# LDFLAGS = "-static"

do_unpack:append() {
}

rename_acconfigure() {
	mv ${S}/aconfigure.ac ${S}/configure.ac
}
do_unpack[postfuncs] += "rename_acconfigure"

EXTRA_AUTORECONF += "\
    -I build \
    --exclude=autoheader \
"

EXTRA_OECONF += "\
    --enable-shared \
"

## Not SMP safe
PARALLEL_MAKE = ""

LD = "${CC}"
CXXFLAGS += "-Wno-deprecated"

CLEANBROKEN = "1"

