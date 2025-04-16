FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SYSTEMD_SERVICE:${PN} += " \
    pipewire.service \
    pipewire-pulse.service \
"

SYSTEMD_AUTO_ENABLE:${PN} = "enable"
