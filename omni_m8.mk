$(call inherit-product, device/htc/m8/full_m8.mk)

# Inherit some common Omnirom stuff.
$(call inherit-product, vendor/omni/config/common.mk)
$(call inherit-product, vendor/omni/config/gsm.mk)

PRODUCT_NAME := omni_m8
