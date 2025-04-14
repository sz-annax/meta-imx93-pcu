FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SYSTEMD_SERVICE:${PN} += " \
    pipewire.service \
    pipewire.socket \
    pipewire-pulse.service \
    pipewire-pulse.socket \
"

SYSTEMD_AUTO_ENABLE:${PN} = "enable"
