# 基于el-form表单实现登录表单

### 表单校验

登录表单的校验包括：

1. 用户名不能为空
2. 用户名长度在1到80个字符之间
3. 密码不能为空
4. 密码长度在5到40个字符之间

代码如下：
```typescript
interface CookieAuthnForm {
  username: string,
  password: string
}

const cookieAuthnForm = reactive<CookieAuthnForm>({
  username: '',
  password: ''
})

const cookieAuthnRules = reactive<FormRules<CookieAuthnForm>>({
  username: [
    { required: true, message: 'Please input username', trigger: 'blur' },
    { min: 1, max: 80, message: 'Length should be 1 to 80', trigger: 'blur' },
  ],
  password: [
    { required: true, message: 'Please input password', trigger: 'blur' },
    { min: 5, max: 40, message: 'Length should be 5 to 40', trigger: 'blur' },
  ]
})
```

<br>

### 表单提交

表单提交时，需要对表单进行校验，校验通过后，调用登录接口。代码如下：

```typescript
const submitCookieAuthnForm = (formEl: FormInstance | undefined) => {
  formEl?.validate((valid) => {
    if (valid) {
      resetErrMsg()
      cookieAuthn(cookieAuthnForm)
    } else {
      return false
    }
  })
}
```

<br>

### 参考资料
1. [el-form表单](https://element-plus.org/zh-CN/component/form.html)
