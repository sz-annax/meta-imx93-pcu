FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://colors.sh \
            file://fstab \
            file://fw_env.config \
            file://my-echo-cancel.conf \
            "

FILESYSTEM_PERMS_TABLES:remove = " files/fs-perms-volatile-log.txt files/fs-perms-volatile-tmp.txt"

do_install:append() {
    # colors.sh
    install -d ${D}${sysconfdir}/profile.d
    install -m 0755 ${WORKDIR}/sources/colors.sh ${D}${sysconfdir}/profile.d/colors.sh

    # fw_env.config
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/sources/fw_env.config ${D}${sysconfdir}

    # my-echo-cancel.conf
    install -d ${D}${sysconfdir}/pipewire/pipewire.conf.d
    install -m 0644 ${WORKDIR}/sources/my-echo-cancel.conf ${D}${sysconfdir}/pipewire/pipewire.conf.d

    install -d ${D}/boot/firmware
    install -d ${D}/mnt/param
    install -d ${D}/mnt/data/uploads
}
