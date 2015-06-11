import com.eclecticlogic.pedal.loader.dm.ExoticTypes
import com.eclecticlogic.pedal.loader.dm.Status
import com.eclecticlogic.pedal.loader.dm.Widget


output = load('a': 'simple.loader.groovy', 'b': 'simple.loader.groovy')
assert output.a.simple1.amount == 10

myIndex = 101
inputReaderVars = withInput(['index': myIndex]).load('input.reader.groovy')
assert inputReaderVars.inputReaderReturn.amount == 101000

def name = 'pedal'

def i = 0;
3.times {
    def tone = table (ExoticTypes, ['login', 'countries', 'authorizations', 'scores', 'custom']) {
        defaultRow {
            it.status = Status.ACTIVE
        }
        row "${name}_${i++}", [true, false, true, false, false, false, false], ['create', 'update'], [1, 2, 3, 6, 10], 'abc'
    }
    def et = tone[0]
    
    table(Widget, ['name', 'type']) {
        widget = row 'abc', et
    }
    
    foundWidget = find(Widget, 1)
}

