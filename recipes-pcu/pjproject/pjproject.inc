## --*- bitbake -*--

SRC_URI = "https://github.com/pjsip/pjproject/archive/refs/tags/${PV}.tar.gz"

LICENSE = "CLOSED"

# v4l-utils ffmpeg libv4l libsdl2
DEPENDS = "alsa-lib v4l-utils ffmpeg libv4l libsdl2"
RDEPENDS:${PN} = "alsa-lib v4l-utils ffmpeg libv4l libsdl2"

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

# do_compile() {
#    oe_runmake dep
#    oe_runmake
# }
