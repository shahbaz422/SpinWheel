# Advanced Spin Wheel
A modern and highly customizable library to implement the Spinner wheel feature in your Android app which comes packed with spinning sound as well.

### Preview:

https://github.com/shahbaz422/AdvancedSpinWheel/assets/89563148/5ef970e1-0712-43fb-9b89-df73ef1d1ea4

Image             |        
:-------------------------:
![image](https://github.com/shahbaz422/AdvancedSpinWheel/assets/89563148/2aaf97ed-762d-40cd-a506-41bd36628406)  


### Implementation : 
```
 implementation 'com.github.shahbaz422:AdvancedSpinWheel:[latest_version]'

```

### How to use the Spinner: 
`all the customizations can be done while creating the object`



     private fun setSpinner() {
        val luckySpinnerView = binding.luckySpinnerView
        val colors = listOf("#D32F2F", "#eb5a4f")
        val colorCount = colors.size
        
        for (i in 1..12) {
            val spinWheelItemSectionModel =
                SpinWheelItemSectionModel()
            spinWheelItemSectionModel.topText = i.toString()
            spinWheelItemSectionModel.secondaryText = if (i % 2 == 0) "Yes" else "No"
            val colorIndex = (i - 1) % colorCount
            spinWheelItemSectionModel.color = Color.parseColor(colors[colorIndex])
            data.add(spinWheelItemSectionModel)
        }
        
        luckySpinnerView.setData(data)
        luckySpinnerView.setRound(5)
        
        //before every spin, you need to provide the target index where you want your spinner to be stopped.
        
        binding.play.setOnClickListener {
            val index: Int = getRandomIndex()
            luckySpinnerView.startLuckyWheelWithTargetIndex(index)
        }

        //you will get the value of result in this callback.
        luckySpinnerView.setLuckyRoundItemSelectedListener { index ->
            Toast.makeText(
                applicationContext,
                "You got index ${data[index].topText}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


`Below is the custom view`

    <com.androtech.spinnertest.custom.spinner.LuckySpinnerView
        android:id="@+id/lucky_spinner_view"
        android:layout_width="320dp"
        android:layout_height="320dp"
        android:layout_centerInParent="true"
        android:elevation="12dp"
        app:lswBackgroundColor="#8045E8"
        app:lswCursor="@drawable/spinx"
        app:lswEdgeColor="#793BE6"
        app:lswEdgeWidth="0"
        app:lswPlaySound="true"
        app:lswTopTextColor="@color/white" />
  

### Contributions are welcomed
