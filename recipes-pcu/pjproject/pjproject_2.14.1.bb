
LICENSE = "CLOSED"

SRC_URI = "https://github.com/pjsip/pjproject/archive/refs/tags/${PV}.tar.gz"
SRC_URI += "\
    file://arm-arch.patch \
    file://makefile.patch \
    file://pj-video.patch \
    file://config_site.h;subdir=${S}/pjlib/include/pj \
"

SRC_URI[sha256sum] = "6140f7a97e318caa89c17e8d5468599671c6eed12d64a7c160dac879ba004c68"

DEPENDS = "alsa-lib"

inherit autotools-brokensep pkgconfig

LDFLAGS = "-shared"

## Not SMP safe
PARALLEL_MAKE = ""

EXTRA_OECONF += "\
    --enable-shared \
"
EXTRA_AUTORECONF += "\
    -I build \
    --exclude=autoheader \
"

CLEANBROKEN = "1"

LD = "${CC}"
CXXFLAGS += "-Wno-deprecated"

do_unpack:append() {
}

rename_acconfigure() {
	mv ${S}/aconfigure.ac ${S}/configure.ac
}
do_unpack[postfuncs] += "rename_acconfigure"

# FILES_${PN} += "/usr/lib/*"

# INSANE_SKIP:${PN} = "ldflags"
# INSANE_SKIP:${PN}-dev = "ldflags"

ALLOW_EMPTY:${PN} = "1"
ALLOW_EMPTY:${PN}-dev = "1"
ALLOW_EMPTY:${PN}-dbg = "1"
ALLOW_EMPTY:${PN}-staticdev = "1"
