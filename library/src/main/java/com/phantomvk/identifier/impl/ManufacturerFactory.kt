package com.phantomvk.identifier.impl

import com.phantomvk.identifier.impl.Devices.isAsus
import com.phantomvk.identifier.impl.Devices.isBlackShark
import com.phantomvk.identifier.impl.Devices.isCoosea
import com.phantomvk.identifier.impl.Devices.isEmui
import com.phantomvk.identifier.impl.Devices.isFlyme
import com.phantomvk.identifier.impl.Devices.isFreeme
import com.phantomvk.identifier.impl.Devices.isHonor
import com.phantomvk.identifier.impl.Devices.isHuawei
import com.phantomvk.identifier.impl.Devices.isLenovo
import com.phantomvk.identifier.impl.Devices.isMeizu
import com.phantomvk.identifier.impl.Devices.isMiui
import com.phantomvk.identifier.impl.Devices.isMotorola
import com.phantomvk.identifier.impl.Devices.isNubia
import com.phantomvk.identifier.impl.Devices.isOnePlus
import com.phantomvk.identifier.impl.Devices.isOppo
import com.phantomvk.identifier.impl.Devices.isOppoRom
import com.phantomvk.identifier.impl.Devices.isQiku
import com.phantomvk.identifier.impl.Devices.isSamsung
import com.phantomvk.identifier.impl.Devices.isSsui
import com.phantomvk.identifier.impl.Devices.isVivo
import com.phantomvk.identifier.impl.Devices.isXiaomi
import com.phantomvk.identifier.impl.Devices.isZTE
import com.phantomvk.identifier.manufacturer.AbstractProvider
import com.phantomvk.identifier.manufacturer.AsusProvider
import com.phantomvk.identifier.manufacturer.CoolpadProvider
import com.phantomvk.identifier.manufacturer.CooseaProvider
import com.phantomvk.identifier.manufacturer.FreemeProvider
import com.phantomvk.identifier.manufacturer.GmsProvider
import com.phantomvk.identifier.manufacturer.HonorProvider
import com.phantomvk.identifier.manufacturer.HonorSettingsProvider
import com.phantomvk.identifier.manufacturer.HuaweiSdkProvider
import com.phantomvk.identifier.manufacturer.HuaweiServiceProvider
import com.phantomvk.identifier.manufacturer.HuaweiSettingsProvider
import com.phantomvk.identifier.manufacturer.LenovoProvider
import com.phantomvk.identifier.manufacturer.MeizuProvider
import com.phantomvk.identifier.manufacturer.MsaProvider
import com.phantomvk.identifier.manufacturer.NubiaProvider
import com.phantomvk.identifier.manufacturer.OppoColorOsProvider
import com.phantomvk.identifier.manufacturer.OppoHeyTapProvider
import com.phantomvk.identifier.manufacturer.QikuBinderProvider
import com.phantomvk.identifier.manufacturer.QikuServiceProvider
import com.phantomvk.identifier.manufacturer.SamsungProvider
import com.phantomvk.identifier.manufacturer.VivoProvider
import com.phantomvk.identifier.manufacturer.XiaomiProvider
import com.phantomvk.identifier.manufacturer.XtcProvider
import com.phantomvk.identifier.manufacturer.ZteProvider
import com.phantomvk.identifier.model.ProviderConfig

object ManufacturerFactory {

  fun getProviders(config: ProviderConfig): List<AbstractProvider> {
    val providers = LinkedHashSet<AbstractProvider>()

    if (isAsus()) {
      val provider = AsusProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    val coolpadProvider = CoolpadProvider(config)
    if (coolpadProvider.isSupported()) {
      providers.add(coolpadProvider)
    }

    if (isFreeme()) {
      val provider = FreemeProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    if (isHonor()) {
      val provider = HonorProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }

      val settingsProvider = HonorSettingsProvider(config)
      if (settingsProvider.isSupported()) {
        providers.add(settingsProvider)
      }
    }

    if (isHuawei() || isEmui()) {
      val provider = HuaweiSdkProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }

      val settingsProvider = HuaweiSettingsProvider(config)
      if (settingsProvider.isSupported()) {
        providers.add(settingsProvider)
      }

      val serviceProvider = HuaweiServiceProvider(config)
      if (serviceProvider.isSupported()) {
        providers.add(serviceProvider)
      }
    }

    if (isLenovo() || isMotorola()) {
      val provider = LenovoProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    if (isXiaomi() || isBlackShark() || isMiui()) {
      val provider = XiaomiProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    if (isNubia()) {
      val provider = NubiaProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    if (isOppo() || isOppoRom() || isOnePlus()) {
      val heyTapProvider = OppoHeyTapProvider(config)
      if (heyTapProvider.isSupported()) {
        providers.add(heyTapProvider)
      }

      val colorOsProvider = OppoColorOsProvider(config)
      if (colorOsProvider.isSupported()) {
        providers.add(colorOsProvider)
      }
    }

    if (isVivo()) {
      val provider = VivoProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    if (isQiku()) {
      val provider = QikuServiceProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }

      val serviceProvider = QikuBinderProvider(config)
      if (serviceProvider.isSupported()) {
        providers.add(serviceProvider)
      }
    }

    if (isSamsung()) {
      val provider = SamsungProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    if (isMeizu() || isFlyme()) {
      val provider = MeizuProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    if (isCoosea()) {
      val provider = CooseaProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    if (isZTE()) {
      val provider = ZteProvider(config)
      if (provider.isSupported()) {
        providers.add(provider)
      }
    }

    if (isZTE() || isSsui()) {
      val msaProvider = MsaProvider(config)
      if (msaProvider.isSupported()) {
        providers.add(msaProvider)
      }
    }

    val xtcProvider = XtcProvider(config)
    if (xtcProvider.isSupported()) {
      providers.add(xtcProvider)
    }

    val msaProvider = MsaProvider(config)
    if (msaProvider.isSupported()) {
      providers.add(msaProvider)
    }

    val provider = GmsProvider(config)
    if (provider.isSupported()) {
      providers.add(provider)
    }

    return providers.toList()
  }
}