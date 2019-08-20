export function getEditContent(h, data, node) {
  let self = this
  return (
    <span class="ly-visible">
      <el-button
        size="mini"
        type="text"
        on-click={ () => self.close(data, node) }
      >
        取消
      </el-button>
      <el-button
        size="mini"
        type="text"
        on-click={ () => self.editMsg(data, node) }
      >
        确认
      </el-button>
    </span>
  )
}

export function getDefaultContent(h, data, node) {
  let self = this
  return (
    <div class="ly-visible" style="right">
      {
        (<span>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            on-click={ () => self.update(node, data) }
          >
            编辑
          </el-button>

          {/*{
            data.level !== 6 &&
            <el-button
              size="mini"
              type="text"
              icon="el-icon-plus"
              on-click={ () => self.append(node, data) }
            >
              添加
            </el-button>
          }*/}

          {
            data.level !== 1 &&
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              on-click={ () => self.remove(node, data) }
            >
              删除
            </el-button>
          }
        </span>)
      }
    </div>
  )
}

export let delItem = (data, payload) => {
    for(let i = 0; i < data.length; i++) {
        if (data[i].id === payload.id) {
            data.splice(i, 1)
            break
        }
        if (data[i].child && data[i].child.length) {
            delItem(data[i].child, payload)
        }
    }
}

export let addItem = (data, payload) => {
    let addObj
    for(let i = 0; i < data.length; i++) {
        if (data[i].id === payload.id) {
            addObj = {
                id: id++,
                name: payload.name,
                level: data[i].level + 1,
                child: []
            }
            data[i].child.unshift(addObj)
            break
        }

        if (data[i].child && data[i].child.length) {
            addItem(data[i].child, payload)
        }
    }
}

export let updateItem = (data, payload) => {
    for(let i = 0; i < data.length; i++) {
        if (data[i].id === payload.id) {
            data[i].name = payload.name
            break
        }

        if (data[i].child && data[i].child.length) {
            updateItem(data[i].child, payload)
        }
    }
}
