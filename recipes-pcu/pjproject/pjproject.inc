## --*- bitbake -*--
SRC_URI = "https://github.com/pjsip/pjproject/archive/refs/tags/${PV}.tar.gz"

DEPENDS += "alsa-lib"

inherit autotools-brokensep pkgconfig

LDFLAGS = "-shared"

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

python populate_packages:prepend () {
    libdir = d.expand("${libdir}")
    pnbase = d.expand("${PN}-lib%s")
    do_split_packages(d, libdir, '^lib(.*)\.so\..*', pnbase, '${BPN} %s library',
                      prepend=True, extra_depends = '', allow_links=True)
}
