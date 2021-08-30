# Cowpay Android


## Requirements
- Android minimum SDK 21
- Target API level 30



## Installation

Add JitPack repository to the project level `build.gradle` file:
```gradle
// project gradle file
allprojects {
 repositories {
  ...
  maven { url 'https://jitpack.io' }
 }
}
```

Add Frames SDK dependency to the module gradble file:
```gradle
// module gradle file
dependencies {
 implementation 'com.github.cowpay-me:Android-SDK:1.1.0'
}
```


## Usage



**Step1** Initialise the sdk.
```kotlin
// you can select your Enviroment (STAGING or PRODUCTION)
   CowpaySDK.init(authorizationToken, merchantCode, haskey, CowpayEnviroment.STAGING)
```

**Step2** Initialise the payment info.
```kotlin
//
   val payment = PaymentInfo(
                   merchantReferenceId = merchantReferenceId,
                   customerMerchantProfileId = "15",
                   amount = 1.0,
                   description = "example description - new android sdk",
                   customerName = "ahmed bassiouny",
                   customerEmail = "customer@customer.com",
                   customerMobile = "01234567890")
```

**Step3** launch sdk and create a callback
```kotlin
        CowpaySDK.launch(this,payment,object :CowpayCallback{
            override fun successByFawry(fawry: Fawry) {
                // Successful payment using Fawry
            }

            override fun successByCreditCard(card: Card) {
                // Successful payment using debit or credit card
            }

            override fun successByCashCollection(cashCollection: CashCollection) {
                // Successful payment using Cash Collection
                Log.e("ahmed",cashCollection.cowpayReferenceId.toString())
            }

            override fun error(it: String) {
                // error during the payment process
            }

            override fun closeByUser() {
                // the user decided to leave the payment page
            }
        })
    }
```

## Additional Options


**1** You can choose the payment methods that you want to appear to the user.
```kotlin
// you can select from 3 options (CowpaySDKOption.CARD,CowpaySDKOption.FAWRY,CowpaySDKOption.CASH_COLLECTION)
// by default we will show 3 options
// add this line before launch sdk
CowpaySDK.setAvailableOption(arrayOf(CowpaySDKOption.FAWRY,CowpaySDKOption.CASH_COLLECTION))
```

**2** You can change success message for cash collection option.
```kotlin
CowpaySDK.setSuccessMsgForCashCollection("Success")
```