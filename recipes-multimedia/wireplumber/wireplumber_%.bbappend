FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SYSTEMD_SERVICE:${PN} += " \
    wireplumber.service \
"

SYSTEMD_AUTO_ENABLE:${PN} = "enable"
