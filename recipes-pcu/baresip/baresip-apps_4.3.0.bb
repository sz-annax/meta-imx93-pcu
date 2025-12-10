SUMMARY = "Baresip - A modular SIP User-Agent"
HOMEPAGE = "https://github.com/baresip/baresip"
LICENSE = "CLOSED"

SRC_URI = "git://github.com/baresip/baresip-apps.git;protocol=https;branch=main \
           file://0001-update-codec-handling-to-support-L16-payload-type.patch \
           "
# v4.3.0 - 2025-11-19
SRCREV = "2ad949eca9985a2cf5f1ce8dbc12712a4edd2403"

S = "${WORKDIR}/git"

DEPENDS = "baresip"

inherit cmake

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release -DCMAKE_PROGRAM_PATH=${STAGING_BINDIR_NATIVE}"

FILES:${PN} += " \
    ${libdir}/baresip/modules/*.so \
"
