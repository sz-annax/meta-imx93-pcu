SUMMARY = "i.MX93 FRDM Python Application Service"
DESCRIPTION = "FRDM Python application that runs as a systemd service."
LICENSE = "CLOSED"

SRC_URI = "git://git@github.com/zhuzhengjing/frdm-app.git;protocol=ssh;branch=main \
           file://frdm-app.service \
           file://my-echo-cancel.conf \
           file://10-ak4619-audio.conf \
        "
SRCREV = "dfcdc6199d82194c1c0e800703604159e177c4dc"

S = "${WORKDIR}/git"

RDEPENDS:${PN} = " python3-pygobject"

do_install() {
    # delete unnecessary files and folders
    rm -rf ${S}/docs ${S}/script

    # /usr/share/frdm-app
    install -d ${D}${datadir}/${PN}
    cp -r ${S}/* ${D}${datadir}/${PN}/

    # systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/${PN}.service ${D}${systemd_system_unitdir}/${PN}.service

    # pipewire config: echo-cancel module + clock/ALSA tuning
    install -d ${D}${sysconfdir}/pipewire/pipewire.conf.d
    install -m 0644 ${UNPACKDIR}/my-echo-cancel.conf ${D}${sysconfdir}/pipewire/pipewire.conf.d
    install -m 0644 ${UNPACKDIR}/10-ak4619-audio.conf ${D}${sysconfdir}/pipewire/pipewire.conf.d
}

inherit systemd

SYSTEMD_SERVICE:${PN} = "${PN}.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
