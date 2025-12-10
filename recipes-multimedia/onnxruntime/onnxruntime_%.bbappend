SRCREV = "a6651273cf4ff3565036691a891d57872c0b761f"

#inherit setuptools3

#DEPENDS += "python3-setuptools python3-setuptools-native python3-wheel"
#RDEPENDS:${PN} += "python3-setuptools"

EXTRA_OECMAKE += "\
    -DCMAKE_SYSTEM_PROCESSOR=aarch64 \
"
