## --*- bitbake -*--

SRC_URI = "https://github.com/pjsip/pjproject/archive/refs/tags/${PV}.tar.gz"

LICENSE = "CLOSED"

# v4l-utils ffmpeg libv4l libsdl2
DEPENDS = "alsa-lib v4l-utils ffmpeg libv4l libsdl2"
RDEPENDS:${PN} = "alsa-lib v4l-utils ffmpeg libv4l libsdl2"

inherit autotools-brokensep pkgconfig

## Not SMP safe
PARALLEL_MAKE = ""

CLEANBROKEN = "1"

python populate_packages:prepend () {
    libdir = d.expand("${libdir}")
    pnbase = d.expand("${PN}-lib%s")
    do_split_packages(d, libdir, '^lib(.*)\.so\..*', pnbase, '${BPN} %s library',
                      prepend=True, extra_depends = '', allow_links=True)
}
