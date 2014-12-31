import com.eclecticlogic.pedal.loader.dm.SimpleType


table(SimpleType, ['amount']) {
    inputReaderReturn = simple1 = row (1000 * index)
    simple2 = row 2000
}